#!/usr/bin/python
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer
from SocketServer import ThreadingMixIn
import threading
from os import curdir, sep
import cgi
import simplejson
import pandas as pd
import os.path
import received_data
import ml_model

PORT_NUMBER = 8081

#This class will handles any incoming request from
#the browser 
class myHandler(BaseHTTPRequestHandler):
  
#Handler for the GET requests
  def do_GET(self):
    if self.path=="/":
      self.path="/index.html"

    if self.path=="/index.html":
      mimetype='text/html'
      self.send_response(200)
      self.end_headers()
      self.wfile.write("<html> <header> <title>Welcome to my home page</title> </header> <body> <h1>Welcome to my home page</h1> <form method=\"POST\" action=\"/send\"> <label>Insert your name: </label> <input type=\"text\" name=\"your_name\"/> <input type=\"submit\" value=\"Send\"/> </form> </body> </html>")
    else:
      self.send_error(404,'File Not Found: %s' % self.path)
    return

#Handler for the POST requests
  def do_POST(self):
    if self.path=="/send":
      form = cgi.FieldStorage(
        fp=self.rfile, 
        headers=self.headers,
        environ={'REQUEST_METHOD':'POST',
                     'CONTENT_TYPE':self.headers['Content-Type'],
      })
  
      print "Your name is: %s" % form["your_name"].value
      self.send_response(200)
      self.end_headers()
      self.wfile.write("Thanks %s !" % form["your_name"].value)
    
    if self.path=="/predict":
      datastr = self.rfile.read(int(self.headers['Content-Length']))
      data = simplejson.loads(datastr)
      df_data = pd.DataFrame(data, columns=['uni_id', 'timestamp', 'heart_rate', 'acceleration', 'steps_per_min', 'hour', 'day', 'activity', 'temp', 'humidity', 'clouds', 'level', 'userHR', 'userSTD'])
      uni_id = df_data.iloc[0,0]
      
      rd_filename = 'received_data_' + uni_id + '.csv'
      if os.path.exists(rd_filename):
        with open(rd_filename, 'a') as f:
          df_data.to_csv(f, header=False, index=False)
      else:
        df_data.to_csv(rd_filename, encoding='utf-8', index=False)
        
      
      received_data.preprocess_data(rd_filename)
      print "preprocess received data"
      ans = received_data.aggregate_data(uni_id)
      print ans
      
      if ans != "nope":
        prediction = ml_model.get_prediction(uni_id, ans)
        print prediction
        ans = str(int(ans)) + ":" + str(prediction)

      self.send_response(200)
      self.end_headers()
      self.wfile.write(ans)
      
    if self.path=="/train":
      datastr = self.rfile.read(int(self.headers['Content-Length']))
      data = simplejson.loads(datastr)
      df_data = pd.DataFrame(data, columns=['uni_id', 'timestamp', 'prediction', 'y', 'context'])
      uni_id = df_data.iloc[0,0]
      
      
      df_feedback = df_data.loc[df_data['y'].notnull(), ['timestamp', 'y', 'context']]
        
      df_report = df_data.loc[df_data['y'].isnull(), ['timestamp', 'context']]
      
      
      ml_model.retrain_model(uni_id, df_feedback, df_report)
      
      self.send_response(200)
      self.end_headers()
      self.wfile.write("training complete!")

    return
      
class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    """ This class allows to handle requests in separated threads.
        No further content needed, don't touch this. """


try:
  #Create a web server and define the handler to manage the
  #incoming request
  server = ThreadedHTTPServer(('', PORT_NUMBER), myHandler)
  print 'Started httpserver on port ' , PORT_NUMBER
  
  #Wait forever for incoming htto requests
  server.serve_forever()

except KeyboardInterrupt:
  print '^C received, shutting down the web server'
  server.socket.close()
