FROM node:alpine@sha256:df76a9449df49785f89d517764012e3396b063ba3e746e8d88f36e9f332b1864

LABEL com.docker.skill.api.version="container/v2"
WORKDIR /skill

COPY package.json package-lock.json ./
RUN npm ci
COPY *.cljs ./

ENTRYPOINT ["node_modules/.bin/nbb","app.cljs"]
