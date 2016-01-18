package org.pipseq.rdf.jena.filter;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import org.pipseq.rdf.jena.filter.TestRest;


/*
 * 
 * 
RuleEngine:

Using node.js w/ express,json-middleware, body-parser

app.js:

var express = require('express');
var bodyParser = require('body-parser')
var app = express();
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());

var names = [
{lastName: 'smith', firstName: 'john'}
];

app.get('/', function (req, res) {
  res.send('Hello World!');
});

app.get('/test', function (req, res) {
	console.log("Called");
  res.send('Hello test!');
});

app.post('/func', function(req, res) {
	console.log(req.body)
	
var nameNode = {
    lname : req.body.lastName + 's',
    fname : req.body.firstName + 's'
  }; 
 
names.push(nameNode);
  res.json(nameNode);
});

var server = app.listen(3000, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
});

 * 
 */
class NodeRestTest {

	@Test
	public void test() {
		TestRest n = new TestRest();
		List<NodeValue> args = new ArrayList<NodeValue>();
		n.nodeRest();
	}

}
