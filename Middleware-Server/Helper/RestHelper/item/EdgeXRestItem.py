
class Addressable:
    def __init__(self, origin, name, protocol='HTTP', address='127.0.0.1', port=49090, path='127.0.0.1', publisher='none', user='none', password='none', topic='none'):
        self.origin = origin
        self.name = name
        self.protocol = protocol
        self.address = address
        self.port = port
        self.path = path
        self.publisher = publisher
        self.user = user
        self.password = password
        self.topic = topic

    def getData(self):
        return {
            'origin': self.origin,
            'name': self.name,
            'protocol': self.protocol,
            'address': self.address,
            'port': self.port,
            'path': self.path,
            'publisher': self.publisher,
            'user': self.user,
            'password': self.password,
            'topic': self.topic
        }


class DeviceService:
    def __init__(self, origin, name, description, labels, addressable, streamingServiceName='wowza', EncoderServiceName='kvazaar'):
        self.origin = origin
        self.name = name
        self.description = description
        self.streamingServiceName = streamingServiceName
        self.EncoderServiceName = EncoderServiceName
        self.labels = labels
        self.addressable = addressable

    def getData(self):
        return {
            'origin': self.origin,
            'name': self.name,
            'description': self.description,
            'streamingServiceName': self.streamingServiceName,
            'encoderServiceName': self.EncoderServiceName,
            "labels": self.labels,
            "addressable": self.addressable
        }


class DeviceProfile:
    def __init__(self, origin, name, description, manufacturer, model, labels, commands):
        self.origin = origin
        self.name = name
        self.description = description
        self.manufacturer = manufacturer
        self.model = model
        self.labels = labels
        self.commands = commands

    def getData(self):
        return {
            'origin': self.origin,
            'name': self.name,
            'description': self.description,
            'manufacturer': self.manufacturer,
            'model': self.model,
            "labels": self.labels,
            "commands": self.commands
        }


class DeviceInfo:
    def __init__(self, origin, name, description,
                 adminState, operatingState, labels,
                 addressable, service, profile):
        self.origin = origin
        self.name = name
        self.description = description
        self.adminState = adminState
        self.operatingState = operatingState
        self.labels = labels
        self.addressable = addressable
        self.service = service
        self.profile = profile

    def getData(self):
        return {
            'origin': self.origin,
            'name': self.name,
            'description': self.description,
            'adminState': self.adminState,
            'operatingState': self.operatingState,
            "labels": self.labels,
            "addressable": self.addressable,
            "service": self.service,
            "profile": self.profile
        }


class ValueDescriptor(object):
    def __init__(self, name, description,
                 min_val, max_val, type,
                 uomLabel, defaultValue, formatting,
                 labels):
        self.name = name
        self.description = description
        self.min_val = min_val
        self.max_val = max_val
        self.labels = labels
        self.type = type
        self.uomLabel = uomLabel
        self.defaultValue = defaultValue
        self.formatting = formatting

    def getData(self):
        return {
            'name': self.name,
            'description': self.description,
            'min': self.min_val,
            'max': self.max_val,
            "labels": self.labels,
            "type": self.type,
            "uomLabel": self.uomLabel,
            "defaultValue": self.defaultValue,
            "formatting": self.formatting
        }