# Find My School

- 텍스트 파일을 파싱하여 특정 주제의 단어의 빈도수를 추출해내기

---

- 구현 환경  
Java 1.8  
Gradle  

---

- 사용 텍스트 데이터  
  - 필터링 대상 댓글리스트  
    - 파일명 : `comments.csv`  
  - Komoran 라이브러리 형태소 학습 파일  
    - 파일명 : `user_dic.txt`  
    - 출처 : https://github.com/shin285/KOMORAN  
  - 공공데이터 학교명 데이터 파일  
    - 파일명 : `mySchool.csv`  
    - 출처 : https://www.data.go.kr  

---

- 빌드 방법  
1. gradle 설치  
2. 프로젝트 루트 접근  
3. `gradle build` 명령어 실행  
4. 프로젝트 루트의 `build\libs` 위치에 jar 파일 생성 확인(findMySchool.jar)  

---  

- 구동 방법  
-> cmd나 쉘에서 아래 명령어를 입력  
-> `java -jar findMySchool.jar`(jar 파일 경로)  
-> 사용 텍스트 데이터를 모두 jar파일 위치에 같이 위치.
  - comments.csv
  - user_dic.txt
  - mySchool.csv
