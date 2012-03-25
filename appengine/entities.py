from google.appengine.ext import db

"""
Data type representing a single data point
"""
class Point(db.Model):
    location = db.GeoPtProperty()
    delta = db.GeoPtProperty()
    time = db.IntegerProperty()        # -1 if wireless was not available
    user_id = db.StringProperty()

"""
Prediction for a certain location and movement obtained via machine learning
"""
class Prediction(db.Model):
    location = db.GeoPtProperty()
    delta = db.GeoPtProperty()
    time = db.IntegerProperty()        # -1 if prediction is that wifi will not be available