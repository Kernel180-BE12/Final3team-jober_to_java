import json, os, sys

REPO_URI = os.environ.get('REPO_URI')
NEW_TAG  = os.environ.get('NEW_TAG')
if not REPO_URI or not NEW_TAG:
    sys.exit("Missing REPO_URI/NEW_TAG")

with open('td.json', 'r', encoding='utf-8') as f:
    td = json.load(f)

# 등록 불가(read-only) 필드 제거
for k in ['revision','taskDefinitionArn','status','requiresAttributes',
          'registeredAt','registeredBy','compatibilities']:
    td.pop(k, None)

# 컨테이너 'app' 이미지 교체 (없으면 첫 컨테이너 교체)
replaced = False
for c in td.get('containerDefinitions', []):
    if c.get('name') == 'app':
        c['image'] = f"{REPO_URI}:{NEW_TAG}"
        replaced = True
        break
if not replaced and td.get('containerDefinitions'):
    td['containerDefinitions'][0]['image'] = f"{REPO_URI}:{NEW_TAG}"

with open('td-new.json', 'w', encoding='utf-8') as f:
    json.dump(td, f)

print("Wrote td-new.json")
