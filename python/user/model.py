from google.appengine.ext import ndb

class User(ndb.Model):
    name = ndb.StringProperty()
    money = ndb.FloatProperty()

    def debitar(self, value):
    	self.money -= value

    def creditar(self, value)
    	self.money += value
