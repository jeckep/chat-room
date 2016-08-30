FROM python:2.7.10-slim
MAINTAINER Yevgeniy Poluektov <yevgeniy.v.poluektov@gmail.com>

RUN apt-get update && apt-get install -qq -y build-essential libpq-dev postgresql-client-9.4 --fix-missing --no-install-recommends

ENV INSTALL_PATH /mobydock
RUN mkdir -p $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

COPY . .

VOLUME ["$INSTALL_PATH/mobydock/static"]

CMD gunicorn -b 0.0.0.0:8000 "mobydock.app:create_app()"
