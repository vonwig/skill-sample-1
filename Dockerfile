FROM node:alpine@sha256:b3ca07adf425d043e180464aac97cb4f7a566651f77f4ecb87b10c10788644bb

WORKDIR /skill

COPY package.json package-lock.json ./
RUN npm ci
COPY *.cljs ./

ENTRYPOINT ["node_modules/.bin/nbb","app.cljs"]
