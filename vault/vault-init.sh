#!/bin/sh

set -e
echo "Initializing Vault..."
vault secrets enable -path=secret kv-v2 || true
echo "Loading .env into Vault..."

set --
while IFS='=' read -r key value || [ -n "$key" ]
do
  key=$(echo "$key" | tr -d '\r')
  value=$(echo "$value" | tr -d '\r')
  case "$key" in
    ""|\#*|VAULT_TOKEN|VAULT_ADDR) continue ;;
  esac
  set -- "$@" "$key=$value"
done < /app/.env

vault kv put secret/app "$@"
echo "Vault initialized"