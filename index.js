const express = require('express')
const app = express()
const port = 8080

const handler = async (req) => {console.info(req)}

app.post('/', (req, res) => {
  handler(req)
    .then(() => {res.sendStatus(201)})
    .catch(() => {res.sendStatus(201)})
})

app.listen(port)
