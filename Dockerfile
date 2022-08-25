FROM node:alpine@sha256:9b435939937b0deef5c1f6fcfd1f265aa7a77574388b671fda322e662744472d

LABEL com.docker.skill.api.version="container/v2"
WORKDIR /skill

COPY package.json package-lock.json ./
RUN npm ci
COPY *.cljs ./

ENTRYPOINT ["node_modules/.bin/nbb","app.cljs"]
