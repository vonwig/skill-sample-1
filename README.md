## Important files

| File | Use |
| :--- | :-- |
| skill.yaml | metadata defining the skill |
| Dockerfile | build the image that will be used to handle the subscription or webhook payload |
| package.json/package-lock.json | this Dockerfile uses Node to handle the payload |
| app.cljs | code for the handler |
| datalog/subscription/*.edn | subscription definitions |

## Subscriptions

[on_dockerfile_from.edn](datalog/subscription/on_dockerfile_from.edn) - watch for pushes on Repos with Dockerfiles containing FROM lines that post at dockerhub
[on_sbom_extracted.edn](datalog/subscription/on_sbom_extracted.edn) - watch for a new SBOM on any Image that has been linked to a GitHub commit

## Testing

After enabling skill, make a push to any repo that contains a Dockerfile with a `FROM` line that points at a dockerhub image, or push an image to any register for which the current workspace has an active registry integration.
