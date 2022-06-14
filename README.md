## Important files

| File | Use |
| :--- | :-- |
| skill.yaml | metadata defining the skill |
| skill.package.yaml | controls how the layout is transformed into the skill registration - default strategy should be okay but what is the default strategy? | 
| Dockerfile | build the image that will be used to handle the subscription or webhook payload |
| package.json/package-lock.json | this Dockerfile uses Node to handle the payload |
| app.cljs or index.js | code for the handler |
| datalog/subscription/*.edn | subscription definitions |

