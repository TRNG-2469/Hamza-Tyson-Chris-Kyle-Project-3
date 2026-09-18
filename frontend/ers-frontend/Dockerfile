# Step 1: Build the Angular app
FROM node:22-alpine AS build

# Set the working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the application
COPY . .

# Build the Angular application
RUN npm run build


# Step 2: Serve the Angular app
FROM nginx:alpine

# Copy the Angular build output into Nginx
COPY --from=build /app/dist/ers-frontend/browser /usr/share/nginx/html

# Expose port 80
EXPOSE 80