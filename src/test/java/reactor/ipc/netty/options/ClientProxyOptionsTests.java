/*
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.ipc.netty.options;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.junit.Test;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import reactor.ipc.netty.options.ClientProxyOptions.Proxy;

public class ClientProxyOptionsTests {

	@Test
	public void asSimpleString() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();

		ClientProxyOptions.AddressSpec addrSpec = typeSpec.type(Proxy.HTTP);
		ClientProxyOptions.Builder builder = addrSpec.host("http://proxy").port(456);
		assertThat(builder.build().asSimpleString()).isEqualTo("proxy=HTTP(http://proxy:456)");

		builder = addrSpec.address(new InetSocketAddress("http://another.proxy", 123));
		assertThat(builder.build().asSimpleString()).isEqualTo("proxy=HTTP(http://another.proxy:123)");
	}

	@Test
	public void asDetailedString() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();

		ClientProxyOptions.AddressSpec addrSpec = typeSpec.type(Proxy.HTTP);
		ClientProxyOptions.Builder builder = addrSpec.host("http://proxy").port(456);
		assertThat(builder.build().asDetailedString())
				.isEqualTo("address=http://proxy:456, nonProxyHosts=null, type=HTTP");

		builder = addrSpec.address(() -> new InetSocketAddress("http://another.proxy", 123));
		assertThat(builder.build().asDetailedString())
				.isEqualTo("address=http://another.proxy:123, nonProxyHosts=null, type=HTTP");

		builder.nonProxyHosts("localhost");
		assertThat(builder.build().asDetailedString())
				.isEqualTo("address=http://another.proxy:123, nonProxyHosts=localhost, type=HTTP");
	}

	@Test
	public void toStringContainsAsDetailedString() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();
		ClientProxyOptions.Builder builder = typeSpec.type(Proxy.HTTP)
		                                             .host("http://proxy")
		                                             .port(456);
		assertThat(builder.build().toString()).isEqualTo(
				"ClientProxyOptions{address=http://proxy:456, nonProxyHosts=null, type=HTTP}");
	}

	@Test
	public void getProxyHandlerTypeHttp() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();
		ClientProxyOptions.Builder builder = typeSpec.type(Proxy.HTTP)
		                                             .host("http://proxy")
		                                             .port(456);

		assertThat(builder.build().newProxyHandler()).isInstanceOf(HttpProxyHandler.class);
		assertThat(builder.build().newProxyHandler().proxyAddress().toString()).isEqualTo("http://proxy:456");

		builder.username("test1");
		assertThat(((HttpProxyHandler) builder.build().newProxyHandler()).username()).isNull();

		builder.password(name -> "test2");
		assertThat(((HttpProxyHandler) builder.build().newProxyHandler()).username()).isEqualTo("test1");
		assertThat(((HttpProxyHandler) builder.build().newProxyHandler()).password()).isEqualTo("test2");
	}

	@Test
	public void getProxyHandlerTypeSocks4() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();
		ClientProxyOptions.Builder builder = typeSpec.type(Proxy.SOCKS4)
		                                             .host("http://proxy")
		                                             .port(456);

		assertThat(builder.build().newProxyHandler()).isInstanceOf(Socks4ProxyHandler.class);
		assertThat(builder.build().newProxyHandler().proxyAddress().toString()).isEqualTo("http://proxy:456");

		builder.username("test1");
		assertThat(((Socks4ProxyHandler) builder.build().newProxyHandler()).username()).isEqualTo("test1");
	}

	@Test
	public void getProxyHandlerTypeSocks5() {
		ClientProxyOptions.TypeSpec typeSpec = ClientProxyOptions.builder();
		ClientProxyOptions.Builder builder = typeSpec.type(Proxy.SOCKS5)
		                                           .host("http://proxy")
		                                           .port(456);

		assertThat(builder.build().newProxyHandler()).isInstanceOf(Socks5ProxyHandler.class);
		assertThat(builder.build().newProxyHandler().proxyAddress().toString()).isEqualTo("http://proxy:456");

		builder.username("test1");
		assertThat(((Socks5ProxyHandler) builder.build().newProxyHandler()).username()).isNull();

		builder.password(name -> "test2");
		assertThat(((Socks5ProxyHandler) builder.build().newProxyHandler()).username()).isEqualTo("test1");
		assertThat(((Socks5ProxyHandler) builder.build().newProxyHandler()).password()).isEqualTo("test2");
	}
}
