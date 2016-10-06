from google.appengine.ext import ndb
from user.model import User
from product.model import Product

class Transaction(ndb.Model):
    value = ndb.FloatProperty()
    user = ndb.KeyProperty(kind=User)
    product = ndb.KeyProperty(kind=Product)

    def __init__(self, user, product, value):
        ndb.Model.__init__(self)

        self.user = user.key
        self.product = product.key

        if product:
            self.value = product.price
        else: 
            self.value = value
