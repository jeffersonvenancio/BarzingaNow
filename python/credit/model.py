from google.appengine.ext import ndb, db

class Credit(ndb.Model):
    user_email = ndb.StringProperty()
    value = ndb.FloatProperty()
    operator = ndb.StringProperty()
    date = ndb.DateTimeProperty(auto_now_add=True)
