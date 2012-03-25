from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

import entities

class Learn(webapp.RequestHandler):
    def get(self):
        # TODO: Do machine learning on collected data points
        pass

class Predict(webapp.RequestHandler):
    def get(self):
        # TODO: Given a location and movement direction, how long until wifi 
        pass

application = webapp.WSGIApplication([('/learn', Learn), ('/predict', Predict)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()