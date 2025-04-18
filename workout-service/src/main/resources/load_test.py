import requests
import time
from collections import Counter

#URL = "http://localhost:8081/api/v1/workout/ping"  # BEZ load balancera
URL = "http://localhost:9020/api/v1/food/ping-workout"  # SA load balancerom

count = Counter()
start = time.time()

for i in range(100):
    try:
        response = requests.get(URL, timeout=3)
        if response.status_code == 200:
            text = response.text.strip()
            count[text] += 1
        else:
            count['error'] += 1
    except Exception as e:
        count['exception'] += 1

end = time.time()

print("\nRezultati:")
for key, value in count.items():
    print(f"{key}: {value}")

print(f"\nUkupno vrijeme: {end - start:.2f} sekundi")
