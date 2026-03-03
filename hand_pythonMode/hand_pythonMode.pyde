import cv2
import mediapipe as mp

mp_hands = mp.solutions.hands.Hands()
frame = cv2.imread("image.jpg")
results = mp_hands.process(frame)
