package edu.mayo.qdm.webapp.client
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.*

class RestClient {

    def GET(url, accept="application/json", contentType=JSON, parse=true, params=null){
        def http = new HTTPBuilder(url)

        def response = http.request(Method.GET, contentType ) {
            uri.query = params
            headers.Accept = accept
        }

        parse ? response : response.text
    }

}