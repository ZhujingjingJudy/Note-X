from django.urls import path
from .views import UploadMusicFileView

urlpatterns = [
    path('upload/', UploadMusicFileView.as_view(), name='upload-music-file'),
]
