# syntax=docker/dockerfile:1.4

FROM --platform=$BUILDPLATFORM node:lts AS development
WORKDIR /code
COPY package.json /code/package.json
COPY package-lock.json /code/package-lock.json
RUN npm ci
COPY . /code
ENV CI=true
ENV PORT=3000
CMD [ "npm", "start" ]

FROM development AS build
RUN ["npm", "run", "build"]

FROM nginx:1.13-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /code/build /usr/share/nginx/html
