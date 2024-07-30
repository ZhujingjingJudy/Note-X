from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
import os

class UploadMusicTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.upload_url = '/upload/'

    def test_upload_music_file(self):
        # Assuming you have a test WAV file in your project directory for testing purposes
        file_path = os.path.join(os.path.dirname(__file__), 'sample.wav')
        
        with open(file_path, 'rb') as test_file:
            response = self.client.post(self.upload_url, {'file': test_file}, format='multipart')
        
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('sheet_music', response.data)

    def test_upload_no_file(self):
        response = self.client.post(self.upload_url, format='multipart')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['error'], 'No file provided')

    def test_upload_invalid_file(self):
        # Uploading an invalid file format (e.g., a text file)
        file_path = os.path.join(os.path.dirname(__file__), 'invalid_file.txt')
        
        with open(file_path, 'rb') as test_file:
            response = self.client.post(self.upload_url, {'file': test_file}, format='multipart')
        
        self.assertEqual(response.status_code, status.HTTP_500_INTERNAL_SERVER_ERROR)
