FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --omit=dev

COPY src ./src
COPY public ./public

RUN mkdir -p /app/data

ENV NODE_ENV=production
ENV HOST=0.0.0.0
ENV PORT=3001
ENV SUPPORT_CHAT_DATA_DIR=/app/data

EXPOSE 3001

CMD ["npm", "start"]
