FROM node:20 AS build

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source code
COPY . .

# Build React app
RUN npm run build

# Production stage
FROM node:20

WORKDIR /app

# Install lightweight static server
RUN npm install -g serve

# Copy build files
COPY --from=build /app/build ./build

EXPOSE 3000

# Serve React production build
CMD ["serve", "-s", "build", "-l", "3000"]