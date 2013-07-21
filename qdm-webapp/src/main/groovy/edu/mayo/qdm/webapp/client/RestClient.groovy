package edu.mayo.qdm.webapp.client
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.*

class RestClient {

    def GET(url, contentType=JSON, params=null){
        def http = new HTTPBuilder(url)

        http.request(Method.GET, contentType ) {
            uri.query = params
            headers.Accept = 'application/xml'
        }.text
    }

    def static main(args){
        def c = new RestClient()

        def params = [format:"hqmf", MeasureId:"124"]
        print c.GET("https://ushik.ahrq.gov/rest/meaningfulUse/retrieveHQMFXML", params)
    }

}