from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class HomePage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('Coming soon...')

application = webapp.WSGIApplication([('/.*', HomePage)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()