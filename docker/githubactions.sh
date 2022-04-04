ls
docker-compose -f .docker/docker-compose.yml down --remove-orphans
#docker-compose -f .github/docker/docker-compose-test.yml --env-file ./.env build
docker-compose -f docker/docker-compose.yml run --rm app