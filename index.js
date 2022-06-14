const api = require("@atomist/skill-entry-point")

api.start(async payload => {
  payload.logger.info(`skill id: ${payload.skill.id}`)
  return {status: {code: 0, reason: "hey"}}
});
