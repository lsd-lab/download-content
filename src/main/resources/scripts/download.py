import youtube_dl
import os
import sys

# URL do vídeo do Dailymotion
video_url = sys.argv[1]

#PATH
output_dir = 'uploads'
output_path = os.path.join(output_dir, '%(title)s.%(ext)s')

os.makedirs(output_dir, exist_ok=True)

# Opções de download
ydl_opts = {
    'format': 'best',  # Formato de vídeo de melhor qualidade disponível
    'outtmpl': output_path,  # Nome do arquivo de saída
}

# Baixar o vídeo
with youtube_dl.YoutubeDL(ydl_opts) as ydl:
    ydl.download([video_url])
