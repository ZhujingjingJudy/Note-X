from rest_framework import serializers

class MusicFileSerializer(serializers.Serializer):
    file = serializers.FileField()
