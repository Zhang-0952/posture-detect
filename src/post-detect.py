import cv2
import mediapipe as mp
import _thread
import cvzone
from playsound import playsound
from cvzone.FaceMeshModule import FaceMeshDetector  # 脸部关键点检测方法
import time
import sys

def playVoice(threadName, delay):
    raise Exception("填写缓存区传值")
    # file = "alice.mp3"
    # playsound(file)


mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_holistic = mp.solutions.holistic
cap = cv2.VideoCapture(0)

detector = FaceMeshDetector(maxFaces=1)
with mp_holistic.Holistic(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
    while cap.isOpened():
        time.sleep(0.3)
        success, image = cap.read()
        if not success:
            print("lgnoring empty camera frame.")  # If loading a video, use "break" instead of "continue". continue

        # 检测脸部关键点，返回绘制关键点后的图像img和脸部关键点坐标faces
        img, faces = detector.findFaceMesh(image, draw=False)  # 不绘制关键点
        # print(faces)

        # （4）处理关键点
        if faces:  # 如果检测到了，那就接下去执行

            face = faces[0]  # faces是三维列表，我们只需要第一张脸的所有关键点

            pointLeft = tuple(face[145])  # 左眼关键点坐标
            pointRight = tuple(face[374])  # 右眼坐标

            # 计算两点之间的线段距离w，相当于勾股定理求距离
            w, _ = detector.findDistance(pointRight, pointLeft)  # 返回线段距离和线段信息(两端点和中点的坐标)
            W = 6.3  # 人脸两眼之间的实际平均距离是6.3cm

            """
            焦距f 测试代码
            #定一个距离测量摄像头的实际焦距f
            # d = 40  # 当前人脸距屏幕的距离
            # f = (w * d) / W  # 根据公式计算焦距
            # 
            # print(f'foucus:{f}')
            """
            print(sys.argv[1])
            f = int(sys.argv[1])  # 上一节的代码求焦距，估计一个平均值，作为当前的焦距。这里设定为900

            # 计算人脸距离屏幕的距离
            dis = (W * f) / w
            #print('distance face to screen:', dis)
            if dis <= 35:
                _thread.start_new_thread(playVoice, ("new_thread_1", 0))
                time.sleep(5)
        if cv2.waitKey(5) & 0xFF == 27:
            break
