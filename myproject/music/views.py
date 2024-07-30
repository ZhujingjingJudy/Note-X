from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.parsers import MultiPartParser, FormParser
from .serializers import MusicFileSerializer
import music21
import aubio
import numpy as np
import os
import wave
from midiutil import MIDIFile
from django.http import FileResponse

class UploadMusicFileView(APIView):
    parser_classes = (MultiPartParser, FormParser)

    def post(self, request, *args, **kwargs):
        serializer = MusicFileSerializer(data=request.data)
        if serializer.is_valid():
            music_file = serializer.validated_data['file']
            file_path = self.handle_uploaded_file(music_file)
            musicxml_path, error_message = self.generate_musicxml(file_path)
            if musicxml_path:
                return FileResponse(open(musicxml_path, 'rb'), as_attachment=True, filename=os.path.basename(musicxml_path))
            else:
                return Response({"error": f"Failed to generate MusicXML: {error_message}"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def handle_uploaded_file(self, f):
        upload_dir = 'uploads'
        if not os.path.exists(upload_dir):
            os.makedirs(upload_dir)
        file_path = os.path.join(upload_dir, f.name)
        with open(file_path, 'wb+') as destination:
            for chunk in f.chunks():
                destination.write(chunk)
        return file_path

    def generate_musicxml(self, file_path):
        try:
            # Convert WAV to MIDI
            midi_path, midi_error = self.wav_to_midi(file_path)
            if not midi_path:
                raise ValueError(f"Failed to convert WAV to MIDI: {midi_error}")

            # Convert MIDI to MusicXML
            notes = music21.converter.parse(midi_path)
            musicxml_path = file_path.replace('.wav', '.musicxml')
            notes.write('musicxml', fp=musicxml_path)
            return musicxml_path, None
        except Exception as e:
            error_message = str(e)
            print(f"Error generating MusicXML: {error_message}")
            return None, error_message

    def wav_to_midi(self, file_path):
        try:
            # Read the WAV file
            print(f"Reading WAV file: {file_path}")
            with wave.open(file_path, 'r') as wav_file:
                samplerate = wav_file.getframerate()
                n_channels = wav_file.getnchannels()
                n_frames = wav_file.getnframes()
                signal = wav_file.readframes(n_frames)
                signal = np.frombuffer(signal, dtype=np.int16)
                print(f"WAV file read successfully: {file_path}, {n_frames} frames, {n_channels} channels, {samplerate} Hz")

            # Setup aubio pitch detection
            win_s = 4096
            hop_s = 512
            tolerance = 0.8
            pitch_o = aubio.pitch("yin", win_s, hop_s, samplerate)
            pitch_o.set_unit("midi")
            pitch_o.set_tolerance(tolerance)
            print("Aubio pitch detection initialized")

            # Run pitch detection
            midi_notes = []
            for i in range(0, len(signal), hop_s):
                sample = signal[i:i+hop_s]
                if len(sample) < hop_s:
                    sample = np.pad(sample, (0, hop_s - len(sample)), 'constant', constant_values=0)
                sample = sample.astype(np.float32)
                pitch = pitch_o(sample)[0]
                if pitch > 0:
                    midi_notes.append(pitch)
            print(f"Pitch detection completed: {len(midi_notes)} notes detected")

            if not midi_notes:
                raise ValueError("No MIDI notes detected")

            # Create MIDI file
            midi_path = file_path.replace('.wav', '.mid')
            midi = MIDIFile(1)
            track = 0
            time = 0  # In beats
            midi.addTrackName(track, time, "Track")
            midi.addTempo(track, time, 120)

            for i, note in enumerate(midi_notes):
                midi.addNote(track, 0, int(note), time + i * 0.5, 1, 100)

            with open(midi_path, 'wb') as midi_file:
                midi.writeFile(midi_file)
            print(f"MIDI file created successfully: {midi_path}")

            return midi_path, None
        except Exception as e:
            error_message = str(e)
            print(f"Error converting WAV to MIDI: {error_message}")
            return None, error_message
