FROM node:alpine

WORKDIR /skill

COPY index.js package.json package-lock.json .
RUN npm ci

ENTRYPOINT ["node index.js"]
