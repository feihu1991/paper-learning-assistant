#!/usr/bin/env python3
"""Simple HTTP server with correct MIME types for React apps"""
import http.server
import socketserver
import os

PORT = 5173

class MyHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # Add MIME types for JavaScript modules
        self.send_header('Content-Type', 'text/html')
        self.send_header('Cache-Control', 'no-cache')
        super().end_headers()
    
    def guess_type(self, path):
        if path.endswith('.js'):
            return 'application/javascript'
        elif path.endswith('.mjs'):
            return 'application/javascript'
        elif path.endswith('.css'):
            return 'text/css'
        elif path.endswith('.json'):
            return 'application/json'
        elif path.endswith('.wasm'):
            return 'application/wasm'
        return super().guess_type(path)

os.chdir('C:\\Users\\win\\dev-team\\pm\\dev-team\\shared\\github_deploy\\paper-learning-assistant\\frontend\\dist')

with socketserver.TCPServer(("", PORT), MyHTTPRequestHandler) as httpd:
    print(f"Serving at http://localhost:{PORT}")
    httpd.serve_forever()
