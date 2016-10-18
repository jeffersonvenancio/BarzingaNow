from google.appengine.ext import ndb, db

class Credit(ndb.Model):
    userEmail = ndb.StringProperty()
    value = ndb.FloatProperty()
    operator = ndb.StringProperty()
