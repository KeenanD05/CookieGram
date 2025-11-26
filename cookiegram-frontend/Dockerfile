#FROM node:18 AS build
#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#RUN npm run build --prod

#FROM nginx:alpine
#COPY --from=build /app/dist/cookiegram-frontend/browser/ /usr/share/nginx/html
#COPY nginx.conf /etc/nginx/conf.d/default.conf
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN npm run build

FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist/cookiegram-frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]