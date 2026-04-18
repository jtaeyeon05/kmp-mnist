import os
import random
import sys

import numpy as np
import torch
import torch.nn.functional as F
from loguru import logger
from torchvision import datasets, transforms

import config
import mnist_cnn


logger.remove()
logger.add(sys.stderr, format="<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | <level>{level:<8}</level> | <level>{message}</level>", level="TRACE")


# ========================================
# 테스트 환경 초기화
# ========================================
logger.info("Initializing Training Environment...")

# 디바이스 선택
# noinspection PyArgumentList
if torch.cuda.is_available():
    device = "cuda"
    dtype = torch.float16
elif torch.backends.mps.is_available():
    device = "mps"
    dtype = torch.float16
else:
    device = "cpu"
    dtype = torch.float32
logger.debug(f"Selected the Device (Device: {device}, DType: {dtype})")

# 랜덤 스테이트 적용
random.seed(config.RANDOM_STATE)
np.random.seed(config.RANDOM_STATE)
torch.manual_seed(config.RANDOM_STATE)
if device == "cuda":
    torch.cuda.manual_seed(config.RANDOM_STATE)
    torch.cuda.manual_seed_all(config.RANDOM_STATE)
    torch.backends.cudnn.deterministic = True
    torch.backends.cudnn.benchmark = False
elif device == "mps":
    torch.mps.manual_seed(config.RANDOM_STATE)

# MNIST 데이터셋
should_download = (
        not os.path.exists(config.MNIST_DATASET_PATH) or
        not os.listdir(config.MNIST_DATASET_PATH) or
        config.MUST_DOWNLOAD_DATASET
)
if should_download:
    logger.debug(f"Starting Download...")
mnist_transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.1307,), std=(0.3081,))
])
mnist_test_dataset = datasets.MNIST(
    root=config.MNIST_DATASET_PATH,
    train=False,
    transform=mnist_transform,
    download=should_download,
)
if should_download:
    logger.debug(f"Finished Download MNIST Dataset")

mnist_test_dataloader = torch.utils.data.DataLoader(
    dataset=mnist_test_dataset,
    batch_size=config.BATCH_SIZE,
    shuffle=False,
    drop_last=False,
)
logger.debug(f"Loaded MNIST TEST Dataset ({len(mnist_test_dataset)}, Path: {os.path.abspath(config.MNIST_DATASET_PATH)})")


# ========================================
# 테스트
# ========================================
logger.info("Starting Test...")

# 모델 로드
model = mnist_cnn.MnistCNN().to(device)
criterion = nn.CrossEntropyLoss()

model_path = config.LOADED_MODEL_PATH
if model_path is None:
    all_filenames = os.listdir(config.MODEL_PATH)
    model_filenames = [
        filename
        for filename in all_filenames
        if filename.endswith(".pth") and "cnn" in filename.lower()
    ]
    if not model_filenames:
        model_path = None
        logger.error("Couldn't Find Any Models")
        exit()
    else:
        model_filenames.sort(key=lambda filename: os.path.getmtime(f"{config.MODEL_PATH}/{filename}"))
        model_path = f"{config.MODEL_PATH}/{model_filenames[-1]}"
        logger.debug(f"Auto-selected Model (Path: {model_path})")
model.load_state_dict(torch.load(model_path))

# 테스트
model.eval()
test_loss = 0
correct = 0
total = len(mnist_test_dataset)

with torch.no_grad():
    for data, target in mnist_test_dataloader:
        data, target = data.to(device), target.to(device)
        output = model(data)
        test_loss += F.cross_entropy(output, target, reduction='sum').item()
        pred = output.argmax(dim=1, keepdim=True)
        correct += pred.eq(target.view_as(pred)).sum().item()
test_loss /= total

logger.info(f"Average loss: {test_loss:.6f}, Accuracy: {correct:>5}/{total:>5} ({100. * correct / total:.2f}%)")
