package examples;

import static org.junit.Assert.*;
import org.junit.Test;
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

class TestGet {

	@Test
	public void test() {
		
		// this works!
		//getText('http://www.scubed.cc', '/pipseq/2016/01/forex.ttl', [:])
		
		getText('http://www.pipseq.org', '/2016/01/forex.ttl', [:])
		//getText('http://www.w3.org/2000/01', '/rdf-schema', [:])
		//getText('http://www.pipseq.org', '/forex.rdf', [:])
	}

	def get(){
		def http = new HTTPBuilder('http://www.pipseq.org')

		def html = http.get( path : '/forex.rdf')
		
		println "here"
	}

	def get2(){
		def http = new HTTPBuilder('http://www.google.com')
		
		def html = http.get( path : '/search', query : [q:'Groovy'] )
		
		assert html instanceof groovy.util.slurpersupport.GPathResult
		assert html.HEAD.size() == 1
		assert html.BODY.size() == 1
				println "here"
	}

	def get3(){
		def http = new HTTPBuilder()
		
		http.request( 'http://ajax.googleapis.com', GET, TEXT ) { req ->
		  uri.path = '/ajax/services/search/web'
		  uri.query = [ v:'1.0', q: 'Calvin and Hobbes' ]
		  headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
		  headers.Accept = 'application/json'
		
		  response.success = { resp, reader ->
			assert resp.statusLine.statusCode == 200
			println "Got response: ${resp.statusLine}"
			println "Content-Type: ${resp.headers.'Content-Type'}"
			println reader.text
		  }
		
		  response.'404' = {
			println 'Not found'
		  }
		}
	}
	def getText(String baseUrl, String path, query) {
		return postText(baseUrl, path, query, Method.GET)
	}

	def postText(String baseUrl, String path, query, method = Method.POST) {
		try {
			def ret = null
			def http = new HTTPBuilder(baseUrl)

			// perform a POST request, expecting TEXT response
			http.request(method, ContentType.TEXT) {
				uri.path = path
				uri.query = query
				headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

				// response handler for a success response code
				response.success = { resp, reader ->
					println "response status: ${resp.statusLine}"
					println 'Headers: -----------'
					resp.headers.each { h ->
						println " ${h.name} : ${h.value}"
					}

					ret = reader.getText()

					println 'Response data:'
					println ret
				}
			}
			return ret

		} catch (groovyx.net.http.HttpResponseException ex) {
			ex.printStackTrace()
			return null
		} catch (java.net.ConnectException ex) {
			ex.printStackTrace()
			return null
		}
	}


}
