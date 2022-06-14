FROM node:alpine

WORKDIR /skill

COPY package.json package-lock.json ./
RUN npm ci
COPY *.cljs ./
# COPY index.js ./

ENTRYPOINT ["node_modules/.bin/nbb","app.cljs"]
#ENTRYPOINT ["node", "index.js"]
