package edu.mayo.qdm.webapp.client

import edu.mayo.qdm.webapp.rest.client.SelfSignedSSLSocketFactory
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.conn.scheme.Scheme

import java.security.KeyStore

import static groovyx.net.http.ContentType.JSON

class RestClient {

    def GET(url, accept="application/json", contentType=JSON, parse=true, params=null){
        def http = new HTTPBuilder(url)

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        def sslsf = new SelfSignedSSLSocketFactory(trustStore);

        http.client.connectionManager.schemeRegistry.register(
                new Scheme("https", sslsf, 443))

        def response = http.request(Method.GET, contentType ) {
            uri.query = params
            headers.Accept = accept
        }

        parse ? response : response.text
    }

}