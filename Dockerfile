FROM node:alpine

WORKDIR /skill

COPY package.json package-lock.json ./
RUN npm ci
COPY *.cljs ./

ENTRYPOINT ["node_modules/.bin/nbb","app.cljs"]
