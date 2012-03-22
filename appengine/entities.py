from google.appengine.ext import db


"""
Data type representing a single data point
"""
class Point(db.Model):
    location = db.GeoPtProperty()
    delta = db.GeoPtProperty()
    time = db.IntegerProperty()
    user_id = db.StringProperty()