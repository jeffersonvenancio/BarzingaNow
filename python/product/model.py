from google.appengine.ext import ndb

class Product(ndb.Model):
    description = ndb.StringProperty()
    price = ndb.FloatProperty()
    quantity = ndb.IntegerProperty()
    category = ndb.StringProperty()
    image_url = ndb.StringProperty()

    def to_dict(self):
        result = super(Product, self).to_dict()
        result['id'] = self.key.id()

        return result
