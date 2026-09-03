import urllib.request
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

file_id = "1ngEUH5l0R0c58zZ-y26kTDqBwFv5dr64"
urls = [
    f"https://drive.google.com/uc?export=view&id={file_id}",
    f"https://drive.usercontent.google.com/download?id={file_id}&export=view",
    f"https://lh3.googleusercontent.com/d/{file_id}",
    f"https://lh3.google.com/u/0/d/{file_id}"
]

for url in urls:
    try:
        req = urllib.request.Request(url, headers={
            'User-Agent': 'Mozilla/5.0 (Android; Mobile; rv:109.0) Gecko/114.0 Firefox/114.0'
        })
        resp = urllib.request.urlopen(req, context=ctx, timeout=8)
        content = resp.read(500)
        print(url, resp.status, resp.headers.get("content-type"), "Len:", len(content), content[:50])
    except Exception as e:
        print(url, "Failed:", e)
