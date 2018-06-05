import requests
import json


class VideoFovRestHelper:
    def __init__(self, API_HOST):
        self.API_HOST = API_HOST
        self.API_HEADERS = {'Content-Type': 'application/json'}
        self.events_list = list()

    def __del__(self):
        pass

    def get_events(self, video_name):
        url_count = self.API_HOST + 'event/count'


        resp = requests.get(url=url_count, headers=self.API_HEADERS)
        count = resp.text

        if int(count) > 100:
            count = str(100)

        # print(count)

        url_events = self.API_HOST + 'event/device/'+video_name+'/'+count
        resp = requests.get(url=url_events, headers=self.API_HEADERS)

        # print(resp.text)

        for item in resp.json():
            item_reading = dict()
            for each_reading in item['readings']:
                # print(each_reading)
                if 'timestamp for ' in str(each_reading['name']):
                    item_reading['timestamp'] = each_reading['value']
                    pass
                elif 'yaw for ' in str(each_reading['name']):
                    item_reading['yaw'] = each_reading['value']
                    pass
                elif 'pitch for ' in str(each_reading['name']):
                    item_reading['pitch'] = each_reading['value']
                    pass
            self.events_list.append(item_reading)

        result_event = list()

        for item in self.events_list:
            item_reading_new = dict()
            if dict(item).get('timestamp') is not None:
                # print(str(item['timestamp']))
                time_slice = str(int(item['timestamp']) / int(500)).split('.')[0]
                time_slice = int(time_slice)*500

                # print(str(time_slice))
                item_reading_new['timestamp'] = time_slice
                item_reading_new['yaw'] = item.get('yaw')
                item_reading_new['pitch'] = item.get('pitch')
                result_event.append(item_reading_new)

        # for item in result_event:
        #     print(item)

        return result_event

class VideoInfoRestHelper:

    def __init__(self, API_HOST):
        self.API_HOST = API_HOST
        self.API_HEADERS = {'Content-Type': 'application/json'}

    def __del__(self):
        pass

    def post_addressable(self, addressable_object):
        url = self.API_HOST + 'addressable'
        addressable_data = addressable_object.getData()
        resp = requests.post(url=url, headers=self.API_HEADERS, json=addressable_data)
        print('Adding addressable for "%s"' % addressable_data['name'])
        print(resp.text)
        return resp

    def post_deviceService(self, deviceService_object):
        url = self.API_HOST + 'deviceservice'
        deviceService_data = deviceService_object.getData()
        resp = requests.post(url=url, headers=self.API_HEADERS, json=deviceService_data)
        print('Adding video service for "%s"' % deviceService_data['name'])
        print(resp.text)
        return resp

    def post_deviceProfile(self, deviceProfile_object):
        url = self.API_HOST + 'deviceprofile'
        deviceProfile_data = deviceProfile_object.getData()
        resp = requests.post(url=url, headers=self.API_HEADERS, json=deviceProfile_data)
        print('Adding video profile for "%s"' % deviceProfile_data['name'])
        print(resp.text)
        return resp

    def post_deviceInfo(self, deviceInfo_object):
        url = self.API_HOST + 'device'
        deviceInfo_data = deviceInfo_object.getData()
        resp = requests.post(url=url, headers=self.API_HEADERS, json=deviceInfo_data)
        print('Adding video information for "%s"' % deviceInfo_data['name'])
        print(resp.text)
        return resp

    def post_valueDescriptor(self, valueDescriptor_object):
        url = self.API_HOST + 'valuedescriptor'
        valueDescriptor_data = valueDescriptor_object.getData()
        resp = requests.post(url=url, headers=self.API_HEADERS, json=valueDescriptor_data)
        print('Adding video valuedescriptor for "%s"' % valueDescriptor_data['name'])
        print(resp.text)
        return resp
