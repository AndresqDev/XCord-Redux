#!/usr/bin/env node
/*
 * Full Mineflayer test client for XCord.
 *
 * Usage:
 *   node advanced-bot.js --name AdvBot
 *   node advanced-bot.js --name AdvBot --chat "hello"
 *   node advanced-bot.js --count 2 --stay 20
 */
const mineflayer = require('mineflayer');

const argv = process.argv.slice(2);
function arg(name, def) {
  const i = argv.indexOf('--' + name);
  return i >= 0 && argv[i + 1] ? argv[i + 1] : def;
}

const host = arg('host', '127.0.0.1');
const port = parseInt(arg('port', '25577'), 10);
const baseName = arg('name', 'AdvBot');
const version = arg('version', '1.8.8');
const chat = arg('chat', '');
const count = parseInt(arg('count', '1'), 10);
const delay = parseFloat(arg('delay', '1.2'));
const stay = parseFloat(arg('stay', '15'));

function makeBot(name) {
  const bot = mineflayer.createBot({
    host,
    port,
    username: name,
    version,
    auth: 'offline',
    hideErrors: false,
    physicsEnabled: true,
  });

  bot.on('login', () => console.log(`[${name}] login`));
  bot.on('spawn', () => {
    console.log(`[${name}] spawn at ${bot.entity && bot.entity.position}`);
    if (chat) bot.chat(chat);
  });
  bot.on('chat', (username, message) => {
    if (username !== bot.username) console.log(`[${name}] chat <${username}> ${message}`);
  });
  bot.on('kicked', (reason) => {
    let text = reason;
    try { text = JSON.stringify(reason); } catch (_) {}
    console.log(`[${name}] kicked: ${text}`);
  });
  bot.on('end', (reason) => console.log(`[${name}] end: ${reason}`));
  bot.on('error', (err) => console.log(`[${name}] error: ${err.message}`));
  bot.on('death', () => console.log(`[${name}] death`));
  return bot;
}

const bots = [];
for (let i = 0; i < count; i++) {
  const name = count > 1 ? `${baseName}${i + 1}` : baseName;
  bots.push(makeBot(name));
  if (i < count - 1) {
    const until = Date.now() + delay * 1000;
    while (Date.now() < until) { /* delay next login */ }
  }
}

setTimeout(() => {
  for (const b of bots) {
    console.log(`[${b.username}] quitting`);
    try { b.quit(); } catch (_) {}
  }
  setTimeout(() => process.exit(0), 500);
}, stay * 1000);
