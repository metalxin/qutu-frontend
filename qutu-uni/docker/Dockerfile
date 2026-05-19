FROM nginx:alpine

COPY dist/build/h5 /data

RUN rm /etc/nginx/conf.d/default.conf

COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
