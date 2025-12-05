# FROM node:18 AS build
# WORKDIR /app
# COPY package*.json ./
# RUN npm install
# COPY . .
# RUN npm run build --prod

# FROM nginx:alpine
# COPY --from=build /app/dist/cookiegram-frontend/browser/ /usr/share/nginx/html
# COPY nginx.conf /etc/nginx/conf.d/default.conf


# -----------------------------------------------------
# STAGE 1: Build Angular App
# -----------------------------------------------------
FROM node:18-alpine AS build
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci

# Copy the rest of the app code
COPY . .

# Build for production
RUN npm run build --configuration=production

# -----------------------------------------------------
# STAGE 2: Serve with Nginx
# -----------------------------------------------------
FROM nginx:alpine

# Copy the built Angular files
# Note: The path /dist/cookiegram might vary based on your angular.json "outputPath"
# We use a wildcard (*) to grab whatever folder is inside dist
COPY --from=build /app/dist/*/browser /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]