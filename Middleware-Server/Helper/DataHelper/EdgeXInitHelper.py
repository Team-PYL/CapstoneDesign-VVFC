from Helper.RestHelper import EdgeXRestHelper
from Helper.RestHelper.item import EdgeXRestItem

VIDEO_SERVICE_PREFIX = 'video service for '
VIDEO_PROFILE_PREFIX = 'video profile for '
VIDEO_INFO_PREFIX = 'video info for '
VIDEO_TIMESTAMP_PREFIX = 'timestamp for '
VIDEO_YAW_PREFIX = 'yaw for '
VIDEO_PITCH_PREFIX = 'pitch for '

def initEdgeXWithVideoInformations(videos, videos_types, videos_models, edgex_host='http://localhost:48081/api/v1/', edgex_host2='http://localhost:48080/api/v1/'):
    videoInfo_restHelper = EdgeXRestHelper.VideoInfoRestHelper(API_HOST=edgex_host)
    videoInfo_restHelper2 = EdgeXRestHelper.VideoInfoRestHelper(API_HOST=edgex_host2)

    # POST Addressable
    for idx in range(0, len(videos)):
        videoItem_addressable = EdgeXRestItem.Addressable(origin=1000000000000,
                                                          name=videos[idx])
        videoInfo_restHelper.post_addressable(videoItem_addressable)

    # POST Device Service
    for idx in range(0, len(videos)):
        videoItem_deviceService = EdgeXRestItem.DeviceService(origin=1000000000000,
                                                              name=VIDEO_SERVICE_PREFIX + videos[idx],
                                                              description='video service for video named ' + videos[
                                                                  idx],
                                                              labels=['vr', 'video', 'fov', 'service'],
                                                              addressable={'name': videos[idx]})
        videoInfo_restHelper.post_deviceService(videoItem_deviceService)

    # POST Device Profile
    for idx in range(0, len(videos)):
        videoItem_deviceProfile = EdgeXRestItem.DeviceProfile(origin=1000000000000,
                                                              name=VIDEO_PROFILE_PREFIX + videos[idx],
                                                              description='video profile for video named ' + videos[
                                                                  idx],
                                                              labels=['vr', 'video', 'fov', 'profile'],
                                                              commands=[],
                                                              manufacturer=videos_types[idx],
                                                              model=videos_models[idx])
        videoInfo_restHelper.post_deviceProfile(videoItem_deviceProfile)

    # POST Device Information
    for idx in range(0, len(videos)):
        videoItem_deviceInfo = EdgeXRestItem.DeviceInfo(origin=1000000000000,
                                                        name=VIDEO_INFO_PREFIX + videos[idx],
                                                        description='video information for video named ' + videos[idx],
                                                        labels=['vr', 'video', 'fov', 'info'],
                                                        addressable={'name': videos[idx]},
                                                        adminState='UNLOCKED',
                                                        operatingState='ENABLED',
                                                        service={'name': VIDEO_SERVICE_PREFIX + videos[idx]},
                                                        profile={'name': VIDEO_PROFILE_PREFIX + videos[idx]})
        videoInfo_restHelper.post_deviceInfo(videoItem_deviceInfo)

    # POST Device Information
    for idx in range(0, len(videos)):
        videoItem_valueDescriptor_timestamp = EdgeXRestItem.ValueDescriptor(name=VIDEO_TIMESTAMP_PREFIX + videos[idx],
                                                        description=videos[
                                                            idx],
                                                        min_val='0',
                                                        max_val='150000',
                                                        type='F',
                                                                            uomLabel='timestamp value',
                                                                            defaultValue='0',
                                                                            formatting='%s',
                                                                            labels=['timestamp'])
        videoItem_valueDescriptor_yaw = EdgeXRestItem.ValueDescriptor(name=VIDEO_YAW_PREFIX + videos[idx],
                                                                            description=VIDEO_YAW_PREFIX +
                                                                                        videos[
                                                                                            idx],
                                                                            min_val='-180',
                                                                            max_val='180',
                                                                            type='F',
                                                                            uomLabel='yaw value',
                                                                            defaultValue='0',
                                                                            formatting='%s',
                                                                            labels=['yaw'])
        videoItem_valueDescriptor_pitch = EdgeXRestItem.ValueDescriptor(name=VIDEO_PITCH_PREFIX + videos[idx],
                                                                            description=VIDEO_PITCH_PREFIX +
                                                                                        videos[
                                                                                            idx],
                                                                            min_val='-180',
                                                                            max_val='180',
                                                                            type='F',
                                                                            uomLabel='pitch value',
                                                                            defaultValue='0',
                                                                            formatting='%s',
                                                                            labels=['pitch'])

        videoInfo_restHelper2.post_valueDescriptor(videoItem_valueDescriptor_timestamp)
        videoInfo_restHelper2.post_valueDescriptor(videoItem_valueDescriptor_yaw)
        videoInfo_restHelper2.post_valueDescriptor(videoItem_valueDescriptor_pitch)

