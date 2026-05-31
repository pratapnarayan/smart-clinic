package com.smarthospital.core.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * Switches Tomcat from the default NIO connector to NIO2.
 *
 * Why this is needed on Java 21 + Windows 11:
 *   The default NIO endpoint (Http11NioProtocol) creates a Selector via Selector.open(),
 *   which in Java 21 on Windows uses WEPollSelectorImpl. That implementation creates an
 *   internal pipe using Unix Domain Sockets — which fails on some Windows 11 configurations
 *   with "Unable to establish loopback connection".
 *
 *   NIO2 (Http11Nio2Protocol) uses AsynchronousServerSocketChannel + AsynchronousChannelGroup
 *   instead of a Selector-based Poller — it never calls the failing code path.
 *
 * This is safe for production too; NIO2 is fully supported in Tomcat 10+.
 */
@Component
public class TomcatServerConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
    }
}
