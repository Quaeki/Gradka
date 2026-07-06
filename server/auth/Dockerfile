FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --omit=dev

COPY src ./src

RUN mkdir -p /app/data

ENV NODE_ENV=production
ENV HOST=0.0.0.0
ENV PORT=3002
ENV AUTH_DATA_DIR=/app/data

EXPOSE 3002

CMD ["npm", "start"]
