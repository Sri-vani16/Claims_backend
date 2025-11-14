# Build stage
FROM node:18-alpine as build

WORKDIR /app

# Copy all files
COPY . .

# Install dependencies and build
RUN npm install && npm run build

# Production stage
FROM nginx:alpine

# Copy built app to nginx
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]